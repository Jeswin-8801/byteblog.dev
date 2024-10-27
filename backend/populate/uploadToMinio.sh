#!/bin/sh
upload_file() {
    filename=$(echo "$1" | cut -d '/' -f 3)
    url="http://minio:9000/byte-blog-bucket/images/tags/$filename"
    if "$2"; then
        url="http://minio:9000/byte-blog-bucket/images/$filename"
    fi
    curl -s -X PUT -T $file \
     -H "Host: minio" \
     -H "Date: $(date -R)" \
     -H "Content-Type: application/octet-stream" \
     "$url"
    echo "uploading $filename"
}
for file in ./images/*; do
    upload_file "$file" false
done
echo ""
for file in ./logo/*; do
    upload_file "$file" true
done
