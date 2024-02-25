versionlte() {
    [  "$1" = "`echo -e "$1\n$2" | sort -V | head -n1`" ]
}
versionlt() {
    [ "$1" != "$2" ] && versionlte "$1" "$2"
}

versionlt "$1" "$2"