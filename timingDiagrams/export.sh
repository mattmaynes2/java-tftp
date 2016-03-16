mkdir -p exported
for f in *.dia; do
dia -t png $f;
name="${f%.*}"
mv ${name}.png exported/
done
