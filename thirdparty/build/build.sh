#!/bin/bash



# Change to the directory of this build script.
cd `dirname "$0"`

# Save this directory for later.
TOP="$PWD"

# Set the location where to find or download eclipse.
BASE_LOCATION="$TOP/ext/eclipse"



function show_usage {
cat <<EOF
Usage:
    build.sh 

Options:
    -c   Compress the resulting repository. (Generate artifacts.jar instead of artifacts.xml)

    -r   Publish to the repository located in the specified directory. (default is ./buildRepository)

    -m   Use the specified URL as the 'repository' in the generated map file.

    -n   Do not copy JARs to the repositiory, update the index only.

    -h   Print this help message
EOF
}

# Set the default build directory.
BUILD_REPOSITORY="$TOP/buildRepository"
# Remove the default build directory.
if [ -e "$BUILD_REPOSITORY" ]; then
    rm -rf "$BUILD_REPOSITORY";
fi

# Set the default 'repository' for map.
BUILD_REPO_MIRROR="REPO_MIRROR"

# By default, do not compress repository.
BUILD_REPO_COMPRESS=""

# By default, copy artifacts to repository.
BUILD_REPO_PUBLISH="-publishArtifacts"


while getopts "chm:nr:" OPTION; do
  case "$OPTION" in
    c)
      echo compress
      BUILD_REPO_COMPRESS="-compress"
      ;;

    h)
      echo help
      show_usage
      exit 2
      ;;

    m)
      echo mirror
      BUILD_REPO_MIRROR="$OPTARG"
      ;;

    n)
      echo no publish
      BUILD_REPO_PUBLISH=""
      ;;

    r)
      echo repository
      BUILD_REPOSITORY="$OPTARG"  
      ;;

    *)
      echo "Unrecognized option: $OPTION"
      echo ""
      show_usage
      exit 2
      ;;
  esac
#  shift
done


# Ensure the build repository directory exists.
if [ ! -e "$BUILD_REPOSITORY" ]; then
  mkdir -p "$BUILD_REPOSITORY"
fi
if [ ! -d "$BUILD_REPOSITORY" ]; then
  echo "Repository must be a directory. Aborting."
  exit 1
fi
# Get the absolute path.
cd "$BUILD_REPOSITORY"
BUILD_REPOSITORY="$PWD"
cd "$TOP"


# Install Eclipse if not already.
if [[ -d "$BASE_LOCATION" ]]
then
  echo Build Target consisting of Eclipse with Deltapack already installed.....
else
  mkdir -p ext
  cd ext

  if [[ ! -f eclipse-rcp-indigo-SR2-linux-gtk.tar.gz ]]
    then
      wget http://download.eclipse.org/technology/epp/downloads/release/indigo/SR2/eclipse-rcp-indigo-SR2-linux-gtk.tar.gz
    fi
  if [[ ! -f eclipse-3.7.2-delta-pack.zip ]]
  then
     wget http://archive.eclipse.org/eclipse/downloads/drops/R-3.7.2-201202080800/eclipse-3.7.2-delta-pack.zip
  fi
  tar -xzvf eclipse-rcp-indigo-SR2-linux-gtk.tar.gz
  unzip -o eclipse-3.7.2-delta-pack.zip
  cd ..
fi


echo "Publishing to repository: $BUILD_REPOSITORY"

java -jar $BASE_LOCATION/plugins/org.eclipse.equinox.launcher_*.jar \
   -application "org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher" \
   -metadataRepository "file:/$BUILD_REPOSITORY" \
   -artifactRepository "file:/$BUILD_REPOSITORY" \
   -source "$PWD/.." \
   $BUILD_REPO_PUBLISH \
   $BUILD_REPO_COMPRESS


if [ $? != 0 ]; then
  echo "Error while publishing repository. Aborting."
  exit 1
fi


echo "Generating map file: $BUILD_REPOSITORY/thirdparty.p2.map"

BUILD_REPO_MAP="$BUILD_REPOSITORY/cs-studio-thirdparty.p2.map"

echo "!*** This file was created on" `date +"%B %d, %Y %r %Z"` > "$BUILD_REPO_MAP"

# Don't really like using sed, but here it goes! First sanitize the the mirror URL.

BUILD_REPO_MIRROR=`echo ${BUILD_REPO_MIRROR} | sed -e 's/\\\\/\\\\\\\\/g' -e 's/\//\\\\\//g' -e 's/\&/\\\\\&/g'`

if [ -d "$BUILD_REPOSITORY/plugins" ]; then
  ls -1 "$BUILD_REPOSITORY/plugins" | sed "s/\(.*\)_\(.*\)\.jar/plugin@\1,\2=p2IU,id=\1,version=\2,repository=${BUILD_REPO_MIRROR}\n/g" >> "$BUILD_REPO_MAP"
fi

if [ -d "$BUILD_REPOSITORY/features" ]; then
  ls -1 "$BUILD_REPOSITORY/features" | sed "s/\(.*\)_\(.*\)\.jar/feature@\1,\2=p2IU,id=\1.feature.jar,version=\2,repository=$BUILD_REPO_MIRROR\n/g" >> "$BUILD_REPO_MAP"
fi

