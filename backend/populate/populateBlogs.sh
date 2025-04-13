#!/bin/bash

URL="http://spring-boot:${SPRING_DOCKER_PORT}"
requestJsonPath="./blogs/users/register*.json"
storeTokenDirPath="./blogs/users/tokens"
blogsDirPath="./blogs/markdown"

if [ ! -d "$storeTokenDirPath" ]; then
  mkdir "$storeTokenDirPath"
  echo "Directory '$storeTokenDirPath' created."
fi

signUp() {
  for file in $requestJsonPath; do
    printf "\nsigning up user <%s> as ADMIN\n" "$(jq -r '.username' "$file")"
    curl -s -X POST -H "Content-Type: application/json" -d @"$file" "$URL/auth/sign-up"
    echo ""
  done
}

# param 1 => registerUser JSON path
getTokenStoredPath() {
  echo "$storeTokenDirPath/token-$(echo "$1" | grep -o "admin[0-9]").txt"
}

login() {
  for file in $requestJsonPath; do
    printf "Logging in user <%s>\n" "$(jq -r '.username' "$file")"
    response=$(curl -s -X POST -H "Content-Type: application/json" -d @"$file" "$URL/auth/login")
    echo "${response/access-token/accessToken}" | jq -r '.accessToken' > "$(getTokenStoredPath "$file")"
  done
}

# param 1 => accessTokenFile path
getUserIdFromTokenFile() {
  payload=$(printf "%s=" "$(cut -d '.' -f 2 "$1")" | base64 -di 2>/dev/null)
  printf "%s" "$payload" | jq -r '.user.id'
}

# param 1 => registerUser JSON path
getImageStoredPath() {
  removeRegister=${1/register-/}
  echo "${removeRegister/json/jpg}"
}

updateUser() {
  for file in $requestJsonPath; do
    printf "\nUpdating user info for user <%s>\n" "$(jq -r '.username' "$file")"
    userId="$(getUserIdFromTokenFile "$(getTokenStoredPath "$file")")"
    data=$(jq --arg id "$userId" '. + {id: $id}' "$file")
    token=$(cat "$(getTokenStoredPath "$file")")
    imagePath="$(getImageStoredPath "$file")"
    curl -s -X PUT "$URL/user" \
      -H "Authorization: Bearer $token" \
      -H "Content-Type: multipart/form-data" \
      -F "image=@$imagePath" \
      -F "user=$data;type=application/json"
    echo ""
  done
}

postBlogs() {
  mapfile -t blogFolders < <(find $blogsDirPath -mindepth 1 -maxdepth 1 -type d)
  count=${#blogFolders[@]}
  mapfile -t tokens < <(find "$storeTokenDirPath" -name "*.txt")
  for ((i=0; i<count; i++)); do
    randomId=$((RANDOM % ${#tokens[@]}))
    userId=$(getUserIdFromTokenFile "${tokens[$randomId]}")
    payload=$(jq --arg id "$userId" '.["user-id"] = $id' "${blogFolders[$i]}/payload.json")
    markdownFilePath=$(find "${blogFolders[$i]}" -type f -name "*.mdx")

    printf "\nPosting new blog <%s> for user <%s>\n" "$(basename "$markdownFilePath")" "$(jq -r '.username' "${requestJsonPath/\*/-admin$((randomId + 1))}")"

    # Collect image files
    imageFiles=()
    while IFS= read -r -d '' file; do
      imageFiles+=("$file")
    done < <(find "${blogFolders[$i]}" -type f \( -name "*.jpg" -o -name "*.png" -o -name "*.jpeg" -o -name "*.webp" \) -print0)
    
    # Construct the curl command
    curlCommand=(
      "curl"
      "-s" "-X" "POST"
      "$URL/blog"
      "-H" "Authorization: Bearer $(cat "${tokens[$randomId]}")"
      "-H" "Content-Type: multipart/form-data"
      "-F" "markdown=@$markdownFilePath"
      "-F" "blog=$payload;type=application/json"
    )
    for image in "${imageFiles[@]}"; do
      curlCommand+=("-F" "images=@$image")
    done

    # Execute the curl command
    "${curlCommand[@]}"
    echo ""
  done
}

logOut() {
  for file in "$storeTokenDirPath"/*; do
      userId="$(getUserIdFromTokenFile "$file")"
      printf "\nLogging out user with id <%s>\n" "$userId"
      curl -s -G "$URL/auth/sign-out" \
           -H "Authorization: Bearer $(cat "$file")" \
           --data-urlencode "id=$userId"
      echo ""
  done
}

signUp
echo "------------------------------------------------------------"
login
echo "------------------------------------------------------------"
updateUser
echo "------------------------------------------------------------"
postBlogs
echo "------------------------------------------------------------"
logOut

rm -rf $storeTokenDirPath