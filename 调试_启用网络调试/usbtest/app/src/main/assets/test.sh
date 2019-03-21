#!/system/bin/sh
ls -l;
su;
setprop service.adb.tcp.port 5555;
stop adbd;
start adbd;
exit;
