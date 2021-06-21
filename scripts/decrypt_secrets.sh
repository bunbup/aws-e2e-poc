#!/bin/sh
set -eo pipefail

MOBILEPROVISION=246e6359-7110-4d47-8371-8f17296c784e.mobileprovision
gpg --quiet --batch --yes --decrypt --passphrase="$GPG_PASS" --output ./.github/secrets/$MOBILEPROVISION ./.github/secrets/$MOBILEPROVISION.gpg
gpg --quiet --batch --yes --decrypt --passphrase="$GPG_PASS" --output ./.github/secrets/cert.p12 ./.github/secrets/cert.p12.gpg

mkdir -p ~/Library/MobileDevice/Provisioning\ Profiles

cp ./.github/secrets/$MOBILEPROVISION ~/Library/MobileDevice/Provisioning\ Profiles/

security create-keychain -p "" build.keychain
security import ./.github/secrets/cert.p12 -t agg -k ~/Library/Keychains/build.keychain -P "$GPG_PASS" -A

security list-keychains -s ~/Library/Keychains/build.keychain
security default-keychain -s ~/Library/Keychains/build.keychain
security unlock-keychain -p "" ~/Library/Keychains/build.keychain

security set-key-partition-list -S apple-tool:,apple: -s -k "" ~/Library/Keychains/build.keychain