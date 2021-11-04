#! /usr/bin/env bash

readonly SCRIPT_DIRECTORY="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
readonly DEFAULT_MODULES="./"

readonly UNIFIED_AGENT_JAR="wss-unified-agent.jar"
readonly UNIFIED_AGENT_JAR_URL="https://unified-agent.s3.amazonaws.com/wss-unified-agent.jar"
readonly WHITESOURCE_SIGNATURE='Signed by "CN=whitesource software inc, O=whitesource software inc, STREET=79 Madison Ave, L=New York, ST=New York, OID.2.5.4.17=10016, C=US"'

readonly JDK_11="./jdk-11/"


get_unified_agent() {
  if [[ ! -f "${UNIFIED_AGENT_JAR}" ]]; then
    curl \
      --location \
      --remote-name \
      --remote-header-name \
      "${UNIFIED_AGENT_JAR_URL}"
  fi
  if [[ ! -f "${UNIFIED_AGENT_JAR}" ]]; then
    echo "Could not find downloaded Unified Agent" >&2
    exit 1
  fi

  # Verify JAR signature
  local path_to_verification_output="./jarsigner-output.txt"
  if ! jarsigner -verify -verbose "${UNIFIED_AGENT_JAR}" > "${path_to_verification_output}" ; then
    echo "Could not verify jar signature" >&2
    exit 2
  fi
  if [[ $(grep --count "${WHITESOURCE_SIGNATURE}" "${path_to_verification_output}") -ne 1 ]]; then
    echo "Could not find signature line in verification output" >&2
    exit 3
  fi
}

local_maven_expression() {
  mvn -q -Dexec.executable="echo" -Dexec.args="\${${1}}" --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec
}

get_product_name() {
  local property="project.name"
  if command -v maven_expression >&2 2>/dev/null; then
    maven_expression "${property}"
  else
    local_maven_expression "${property}"
  fi
}

get_project_version() {
  local property="project.version"
  if command -v maven_expression >&2 2>/dev/null; then
    maven_expression "${property}"
  else
    local_maven_expression "${property}"
  fi
}

download_jdk_11() {
  curl \
    --location \
    --remote-name \
    --remote-header-name https://download.java.net/openjdk/jdk11/ri/openjdk-11+28_linux-x64_bin.tar.gz
  tar xaf openjdk-11+28_linux-x64_bin.tar.gz
}

scan_module() {
  local modules="${1}"
  local version="${2}"
  local module_name
  module_name=$(basename "${1}")
  if [[ ! -d "${JDK_11}" ]]; then
    download_jdk_11
  fi
  JAVA_HOME="$(readlink -f ${JDK_11})"
  export JAVA_HOME
  local app_path="${modules}/target/${module_name}-${version}.jar"
  if [[ -f "${app_path}" ]]; then
    java --illegal-access=warn -jar wss-unified-agent.jar -c whitesource.properties -appPath "${app_path}" -d "${modules}"
  else
    echo "Could not find target jar path: ${app_path}" >&2
  fi
  unset JAVA_HOME
}

scan_multi_module() {
  local modules="${1}"
  local app_path="${2}"
  if [[ ! -d "${JDK_11}" ]]; then
    download_jdk_11
  fi
  JAVA_HOME="$(readlink -f ${JDK_11})"
  export JAVA_HOME
  if [[ -f "${app_path}" ]]; then
    java --illegal-access=warn -jar ${UNIFIED_AGENT_JAR} -c whitesource.properties -appPath "${app_path}" -d "${modules}"
  else
    echo "Could not find target jar path: ${app_path}" >&2
  fi
  unset JAVA_HOME
}

scan() {
  WS_PRODUCTNAME=$(get_product_name)
  export WS_PRODUCTNAME
  if [[ -z "${PROJECT_VERSION}" ]]; then
    PROJECT_VERSION=$(get_project_version)
  fi
  export WS_PROJECTNAME="${WS_PRODUCTNAME} ${PROJECT_VERSION%.*}"
  echo "${WS_PRODUCTNAME} - ${WS_PROJECTNAME}"
  local path_to_jar="${SCRIPT_DIRECTORY}/sonar-java-plugin/target/sonar-java-plugin-${PROJECT_VERSION}.jar"
  if [[ -z "${MODULES}" ]]; then
    scan_multi_module DEFAULT_MODULES "${path_to_jar}"
  else
    local split_modules
    IFS="," read -ra split_modules <<< "${MODULES}"
    for module in "${split_modules[@]}"; do
      scan_module "${module}" "${PROJECT_VERSION}"
    done
  fi
  echo "MODULES=${MODULES}"

}

get_unified_agent
scan
