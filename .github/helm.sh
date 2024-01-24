#!/bin/bash

# This function compares two version numbers.
# It takes two version numbers as arguments and returns 0 if the first is greater than the second,
# 1 if the first is less than the second, and 2 if the two are equal.
vercmp() {
  if [[ "$1" == "$2" ]]; then
    return 0
  fi
  local IFS=.
  local i ver1=($1) ver2=($2)
  # fill empty fields in ver1 with zeros
  for ((i = ${#ver1[@]}; i < ${#ver2[@]}; i++)); do
    ver1[i]=0
  done
  for ((i = 0; i < ${#ver1[@]}; i++)); do
    if [[ -z ${ver2[i]} ]]; then
      # fill empty fields in ver2 with zeros
      ver2[i]=0
    fi
    if ((10#${ver1[i]} > 10#${ver2[i]})); then
      return 1
    fi
    if ((10#${ver1[i]} < 10#${ver2[i]})); then
      return 2
    fi
  done
  return 0
}


# This function returns the next release candidate version of a Helm chart.
# It takes the chart name as an argument and returns the next release candidate version.
next_rc_version() {
  local chartName="$1"

  # Get the latest development version of the chart.
  devel=$(helm search repo --devel --regexp "\v$chartName\v" | sed -n 2p | awk '{print $2}')

  # If there is no development version, return the first release candidate version.
  if [ -z "$devel" ]; then
    echo "1.0.0-rc.1"
    return 0
  fi

  # Get the latest stable version of the chart.
  stable=$(helm search repo --regexp "\v$chartName\v" | sed -n 2p | awk '{print $2}')

  # Extract the stable and release candidate versions from the development version.
  devel_stable=$(echo "$devel" | cut -d "-" -f 1)
  devel_rc=$(echo "$devel" | cut -d "-" -f 2)

  # If there is no release candidate version or the release candidate version is the same as the stable version, set the release candidate version to rc.0.
  if [ -z "$devel_rc" ] || [ "$devel_rc" = "$devel_stable" ]; then
    devel_rc="rc.0"
  fi

  # Compare the stable and development versions.
  vercmp "$devel_stable" "$stable"
  ret=$?

  # If the development version is greater than the stable version, increment the release candidate version.
  if [ "$ret" = 1 ]; then
    devel_rc=$(echo "$devel_rc" | awk -F. '{$NF = $NF + 1;} 1' | sed 's/ /./g')
  else
    # If the stable version is greater than or equal to the development version, increment the stable version and set the release candidate version to rc.1.
    devel_stable=$(echo "$stable" | awk -F. '{$NF = $NF + 1;} 1' | sed 's/ /./g')
    devel_rc="rc.1"
  fi

  # Return the next release candidate version.
  echo "$devel_stable-$devel_rc"
  return 0
}

# This function returns the next stable version of a Helm chart.
# It takes the chart name as an argument and returns the next stable version.
next_version() {
  local chartName="$1"

  # Get the latest stable version of the chart.
  stable=$(helm search repo "$chartName" | sed -n 2p | awk '{print $2}')

  # If there is no stable version, return the first version.
  if [ -z "$stable" ]; then
    echo "1.0.0"
    return 0
  fi

  # Increment the last field of the version number and return the next stable version.
  echo "$stable" | cut -d "-" -f 1 | awk -F. '{$NF = $NF + 1;} 1' | sed 's/ /./g'
}

# This case statement takes a command as an argument and calls the corresponding function.
case $1 in
"next-rc-version")
  next_rc_version "$2"
  ;;
"next-version")
  next_version "$2"
  ;;
*)
  echo "Unknown command: $1"
  exit 1
  ;;
esac
