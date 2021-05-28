#!/bin/sh
set -eo pipefail

gpg --quiet --batch --yes --decrypt --passphrase="$GPG_PASS" --output ./.github/secrets/6e742bf5-5ba7-4b72-9204-9c972c7477ee.mobileprovision ./.github/secrets/6e742bf5-5ba7-4b72-9204-9c972c7477ee.mobileprovision.gpg
gpg --quiet --batch --yes --decrypt --passphrase="$GPG_PASS" --output ./.github/secrets/cert.p12 ./.github/secrets/cert.p12.gpg

mkdir -p ~/Library/MobileDevice/Provisioning\ Profiles

cp ./.github/secrets/6e742bf5-5ba7-4b72-9204-9c972c7477ee.mobileprovision ~/Library/MobileDevice/Provisioning\ Profiles/

security create-keychain -p "" build.keychain
security import ./.github/secrets/cert.p12 -t agg -k ~/Library/Keychains/build.keychain -P "$GPG_PASS" -A

security list-keychains -s ~/Library/Keychains/build.keychain
security default-keychain -s ~/Library/Keychains/build.keychain
security unlock-keychain -p "" ~/Library/Keychains/build.keychain

security set-key-partition-list -S apple-tool:,apple: -s -k "" ~/Library/Keychains/build.keychain