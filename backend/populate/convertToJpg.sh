#!/bin/sh

# Function to check if a command is installed
is_command_installed() {
  command -v "$1" >/dev/null 2>&1
}

# Command to check
command="magick"

if is_command_installed "$command"; then
  echo "$command is installed\n"
else
  echo "installing $command"
  sudo mkdir /opt/magick
  sudo wget -q -P /opt/magick https://imagemagick.org/archive/binaries/magick
  sudo chmod +x /opt/magick/magick
  sudo ln -s /opt/magick/magick /usr/local/bin/magick
fi

convertedImagesDirectory="images_converted"

# Check if the directory exists
if [ ! -d "$convertedImagesDirectory" ]; then
  # Create the directory
  mkdir "$convertedImagesDirectory"
  echo "Directory '$convertedImagesDirectory' created."
fi

if [ ! -d "images" ]; then
  echo "No images directory found"
  exit 0
fi

echo "\nConverting all images to .jpg ...\n"

for file in ./images/*; do
  filename=$(echo "$file" | cut -d '/' -f 3 | grep -E -o '[a-z0-9_]+\.' | tr -d '.')
  
  if echo "$file" | grep -q "jpg$"; then
    cp "$file" $convertedImagesDirectory
    echo "$filename.jpg exists"
  else
    magick "$file" "./$convertedImagesDirectory/$filename.jpg"
    echo "converting to > $filename.jpg"
  fi
done

rm -rf ./images
mv $convertedImagesDirectory ./images
