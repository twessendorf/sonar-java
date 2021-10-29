#! /usr/bin/env bash

readonly SCRIPT_DIRECTORY="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

readonly UNIFIED_AGENT_JAR="wss-unified-agent.jar"
readonly UNIFIED_AGENT_JAR_MD5_CHECKSUM="706694E349EA14CB04C4621B70D99A93" # MD5 hash for version 29.9.1.1
readonly UNIFIED_AGENT_JAR_URL="https://unified-agent.s3.amazonaws.com/wss-unified-agent.jar"

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

  # Verify JAR checksum
  local checksum="$(md5sum ${UNIFIED_AGENT_JAR} | cut --delimiter=" " --fields=1 | awk ' { print toupper($0) }')"
  if [[ "${checksum}" != "${UNIFIED_AGENT_JAR_MD5_CHECKSUM}" ]]; then
    echo "MD5 checksum mismatch." >&2
    echo "expected: ${UNIFIED_AGENT_JAR_MD5_CHECKSUM}" >&2
    echo "computed: ${checksum}" >&2
    exit 2
  fi

  # Verify JAR signature
  if ! jarsigner -verify "${UNIFIED_AGENT_JAR}"; then
    echo "Could not verify jar signature" >&2
    exit 3
  fi
}

local_maven_expression() {
  mvn -q -Dexec.executable="echo" -Dexec.args="\${${1}}" --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec
}

get_product_name() {
  local property="project.name"
  if which maven_expression >&2 2>/dev/null; then
    maven_expression "${property}"
  else
    local_maven_expression "${property}"
  fi
}

get_project_version() {
  local property="project.version"
  if which maven_expression >&2 2>/dev/null; then
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
  local folder="${1}"
  local version="${2}"
  local module_name=$(basename "${1}")
  if [[ ! -d "${JDK_11}" ]]; then
    download_jdk_11
  fi
  export JAVA_HOME="$(readlink -f ${JDK_11})"
  local app_path="${folder}/target/${module_name}-${version}.jar"
  if [[ -f "${app_path}" ]]; then
    java --illegal-access=warn -jar wss-unified-agent.jar -c whitesource.properties -appPath "${app_path}" -d "${folder}"
  else
    echo "Could not find target jar path: ${app_path}" >&2
  fi
  unset JAVA_HOME
}

scan() {
  export WS_PRODUCTNAME=$(get_product_name)
  if [[ -z "${PROJECT_VERSION}" ]]; then
    PROJECT_VERSION=$(get_project_version)
  fi
  export WS_PROJECTNAME="${WS_PRODUCTNAME} ${PROJECT_VERSION%.*}"
  echo "${WS_PRODUCTNAME} - ${WS_PROJECTNAME}"
  echo "****************************************************************************************************************************************************"
  #scan_module "${SCRIPT_DIRECTORY}/docs/java-custom-rules-example" "${PROJECT_VERSION}"
  #scan_module "${SCRIPT_DIRECTORY}/external-reports" "${PROJECT_VERSION}"
  #scan_module "${SCRIPT_DIRECTORY}/its/plugin/tests" "${PROJECT_VERSION}"
  #scan_module "${SCRIPT_DIRECTORY}/its/plugin/plugins/java-extension-plugin" "${PROJECT_VERSION}"
  #scan_module "${SCRIPT_DIRECTORY}/its/ruling" "${PROJECT_VERSION}"
  #scan_module "${SCRIPT_DIRECTORY}/java-checks" "${PROJECT_VERSION}"
  #scan_module "${SCRIPT_DIRECTORY}/java-checks-testkit" "${PROJECT_VERSION}"
  #scan_module "${SCRIPT_DIRECTORY}/java-frontend" "${PROJECT_VERSION}"
  #scan_module "${SCRIPT_DIRECTORY}/java-jsp" "${PROJECT_VERSION}"
  #scan_module "${SCRIPT_DIRECTORY}/java-surefire" "${PROJECT_VERSION}"
  #scan_module "${SCRIPT_DIRECTORY}/java-symbolic-execution" "${PROJECT_VERSION}"
  #scan_module "${SCRIPT_DIRECTORY}/jdt" "${PROJECT_VERSION}"
  scan_module "${SCRIPT_DIRECTORY}/sonar-java-plugin" "${PROJECT_VERSION}"
}

get_unified_agent
scan
