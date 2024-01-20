#!/bin/sh
echo "Installing pre-commit hook..."

# this subshell is a scope of try
# try
(
  # this flag will make to exit from current subshell on any error
  # inside it (all functions run inside will also break on any error)
  set -e
  filename=.git/hooks/pre-commit
  cat dry-script/pre-commit.sh >> $filename
  chmod +x $filename
  echo "Done install pre-commit"
)
# and here we catch errors
# catch
errorCode=$?
if [ $errorCode -ne 0 ]; then
  echo "Failed to install pre-commit"
  # We exit the all script with the same error, if you don't want to
  # exit it and continue, just delete this line.
  exit $errorCode
fi
