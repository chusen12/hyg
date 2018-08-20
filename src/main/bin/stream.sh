#!/usr/bin/env bash
#***********************************************************************
# Script  : hyg
# Version : 2.0.1
#***********************************************************************
STREAM_HOME=$(cd `dirname $0`/..; pwd)

# 引入字体颜色
. $STREAM_HOME/bin/Utilities.sh
JAVA_OPTS="-Xmx700M -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:${STREAM_HOME}/logs/gc.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$STREAM_HOME/logs/"

# 打印 help
function print_usage(){
    echo "Usage: stream [COMMAND]:"
    echo " where COMMAND is one of:"
    echo "setup                         setup the environment"
    echo "start                         run the  application"
    echo "stop                          shutdown the  application"
    echo "restart                       restart the  application"
}



function startStream(){

    #首先判断进程文件是否存在(没有则创建一个)
    if [ ! -f "${STREAM_HOME}/logs/hyg_pid.log" ]; then
        touch "${STREAM_HOME}/logs/hyg_pid.log"
    fi
    #获取进程id
    proc_id=`cat ${STREAM_HOME}/logs/hyg_pid.log`
    if [ -z ${proc_id} ]; then
        out "Starting the server..."
        #启动服务
        #1.1获取classpath
        CLASSPATH=`getClassPath`

        cd ${STREAM_HOME}/conf
        # 启动java 主程序
        nohup java -cp ${CLASSPATH} ${JAVA_OPTS}  it.chusen.hyg.analysis.ClassAnalysisApp &>> ${STREAM_HOME}/logs/hyg.out&

        # 获取返回的进程号
        pid=`echo $!`

        sleep 1
        # 查看是否已经有该进程了
        ps -p ${pid} > /dev/null 2>&1
        # 如果有 则启动成功 负责失败!!
        if [ $? -eq 0 ]; then
            echo ${pid} > ${STREAM_HOME}/logs/hyg_pid.log
            chmod 777 ${STREAM_HOME}/logs/hyg.log
            success "Start the server successfully!"
            success "Please check log ${STREAM_HOME}/logs/hyg.log for details."
        else
            error "Start the server failed!"
            error "Please check log ${STREAM_HOME}/logs/hyg.log for details."
        fi
    else
        out "the Server is running..."
    fi
}

function stopStream(){

    #查看进程id
    proc_id=`cat ${STREAM_HOME}/logs/hyg_pid.log`
    if [ -z ${proc_id} ]; then
        out "the server is not running!"
    else
        out "Stop the server...."
        for id in ${proc_id[*]} ; do
            ps -ef | grep ${id} | grep -v grep

            if [ $? -eq 0 ]; then

                kill ${id}

                if [ $? -eq 0 ]; then
                    success "Stop the server successfully."
                    echo > ${STREAM_HOME}/logs/hyg_pid.log
                else
                    error "Stop the server failed! "
                    error "Please check log ${STREAM_HOME}/logs/hyg.log for error details.."
                fi
            else
                out "the server which process id ${id} is not running!!"
                echo > ${STREAM_HOME}/logs/hyg_pid.log
            fi
        done
    fi
}
### 获取classpath
function getClassPath(){
    JAVA_OPTS="-XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:${STREAM_HOME}/logs/gc.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$STREAM_HOME/logs/"

    for jarFile in `ls ${STREAM_HOME}/lib/*jar` ; do
        CLASSPATH=${CLASSPATH}:${jarFile}
    done

    echo ${CLASSPATH}
}

if [ $# -eq 0 ]; then
    print_usage;
else
 case $1 in
 start)
   startStream;;
 stop)
    stopStream;;
 restart)
    stopStream
    startStream;;
 --help | -help | -h)
    print_usage;;
 *)
    error "Invalid argument, please refer to:"
        print_usage;;
 esac

fi
exit 0;
