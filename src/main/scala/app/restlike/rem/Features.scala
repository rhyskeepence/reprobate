package app.restlike.rem

//TODO: Next
//update key
//update value
//tag
//detag
//other standard tagging stuff
//stop using white .. it won't play well on white backgrounds - could have an inverse user setting?

//TODO: Then
//store things per user (think about globlal id vs user id)
//tidy up everything about status, ownership and releases

//TODO: could it be more about creating entities and add KV pairs to them
//TODO: implememt slack style tokens

//TODO: de-dupe
//pull out a clitools jar
//can we share serialisers and persistence? (put in common) .. might be hard

//TODO: handle corrupted rem.json

//TODO: protect against empty value
//TODO: discover common keys and present them when updating
//TODO: be careful with aka .. they need to be unique
//TODO: on update, don't show self in list of others and don't show anything if others are empty
//TODO: make it possible to ask questions and force others to answer them
//TODO: colourise
//http://stackoverflow.com/questions/287871/print-in-terminal-with-colors-using-python?rq=1
//http://apple.stackexchange.com/questions/74777/echo-color-coding-stopped-working-in-mountain-lion
//http://unix.stackexchange.com/questions/43408/printing-colored-text-using-echo
//e.g. printf '%s \e[0;31m%s\e[0m %s\n' 'Some text' 'in color' 'no more color'
//  def red(value: String) = s"\e[1;31m $value \e[0m"

//TODO: (maybe) support curl
//#MESSAGE="(Foo) Deployed ${VERSION} to ${MACHINE_NAME}"
//#curl --connect-timeout 15 -H "Content-Type: application/json" -d "{\"message\":\"${MESSAGE}\"}" http://localhost:8765/broadcast
//#wget --timeout=15 --no-proxy -O- --post-data="{\"message\":\"${MESSAGE}\"}" --header=Content-Type:application/json "http://localhost:8765/broadcast"
