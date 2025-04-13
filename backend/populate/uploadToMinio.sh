#!/bin/bash

uploadFile() {
    filename=$(echo "$1" | cut -d '/' -f 3)
    url="http://minio:${MINIO_SERVER_PORT}/byte-blog-bucket/images/tags/${filename}"
    if "$2"; then
        url="http://minio:${MINIO_SERVER_PORT}/byte-blog-bucket/images/${filename}"
    fi
    curl -s -X PUT -T "$file" \
     -H "Host: minio" \
     -H "Date: $(date -R)" \
     -H "Content-Type: application/octet-stream" \
     "$url"
    echo "uploading $filename"
}

for file in ./images/*; do
    uploadFile "$file" false
done

echo ""

for file in ./logo/*; do
    uploadFile "$file" true
done
