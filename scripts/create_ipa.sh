#!/bin/bash
set -eo pipefail

PROJECT=swiftSdkTest
CURRENT_DIR=`pwd`
TMPDIR=`mktemp -d /tmp/${PROJECT}.XXX` || exit 1
ARCHIVE_PATH=$TMPDIR/${PROJECT}.xcarchive
EXPORT_PATH=$TMPDIR/Payload # DIRECTORY
xcodebuild -workspace ./${PROJECT}.xcworkspace -scheme ${PROJECT} archive -archivePath $ARCHIVE_PATH -verbose
mkdir $EXPORT_PATH
cp -r $ARCHIVE_PATH/Products/Applications/$PROJECT.app $EXPORT_PATH/Payload
cd $EXPORT_PATH && zip -r -X "../Payload.ipa" .
cd $CURRENT_DIR 
mv $TMPDIR/Payload.ipa  .
