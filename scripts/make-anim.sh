sh render-anim.sh $1 "tmp/anim/frame_"
java SpriteAssembler.java "tmp/anim/" $2
rm -r tmp