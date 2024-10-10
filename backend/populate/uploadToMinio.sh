#!/bin/sh

# find ./images -name '*' | egrep -o '[a-z0-9_]+\.' | tr -d '.' | tr '_' ' ' | sed -e 's/\b\(.\)/\u\1/g'

for file in ./images/*; do
    filename=$(echo $file | cut -d '/' -f 3)
    curl -s -X PUT -T $file \
     -H "Host: minio" \
     -H "Date: $(date -R)" \
     -H "Content-Type: application/octet-stream" \
     "http://localhost:9000/byte-blog-bucket/images/tags/$filename"
     echo "uploading $filename"
done
