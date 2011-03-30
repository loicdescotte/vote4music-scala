# Scala
import sys
import inspect
import os
import subprocess
import shutil

from play.utils import *

MODULE = 'scala'

COMMANDS = ['scala:console']

def execute(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")

    if command == "scala:console":
        # add precompiled classes to classpath
        cp_args = app.cp_args() + ":" + os.path.normpath(os.path.join(app.path,'tmp', 'classes'))
        # replace last element with the console app
        java_cmd = app.java_cmd(args, cp_args)
        java_cmd[len(java_cmd)-2] = "play.console.Console"
        java_cmd.insert(2, '-Xmx512M')
        subprocess.call(java_cmd, env=os.environ)
        print

def after(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")

    # ~~~~~~~~~~~~~~~~~~~~~~ new
    if command == 'new':
        shutil.rmtree(os.path.join(app.path, 'app/controllers'))
        shutil.rmtree(os.path.join(app.path, 'app/models'))
        module_dir = inspect.getfile(inspect.currentframe()).replace("commands.py", "")
        shutil.copyfile(os.path.join(module_dir, 'resources', 'controllers.scala'), os.path.join(app.path, 'app', 'controllers.scala'))
        ac = open(os.path.join(app.path, 'conf/application.conf'), 'r')
        conf = ac.read()
        conf = conf + '\n# Disable auto-redirect for scala. It will be the default in the 1.0 version of the scala module\nscala.enableAutoRedirect=false\n'
        ac = open(os.path.join(app.path, 'conf/application.conf'), 'w')
        ac.write(conf)

    # ~~~~~~~~~~~~~~~~~~~~~~ Eclipsify
    if command == 'ec' or command == 'eclipsify':
        dotProject = os.path.join(app.path, '.project')
        replaceAll(dotProject, r'org.eclipse.jdt.core.javabuilder', "ch.epfl.lamp.sdt.core.scalabuilder")
        replaceAll(dotProject, r'<natures>', "<natures>\n\t\t<nature>ch.epfl.lamp.sdt.core.scalanature</nature>")
