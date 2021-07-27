@echo off

set CS_NATIVE_LAUNCHER=true
set IS_CS_INSTALLED_LAUNCHER=true


@REM set %HOME% to equivalent of $HOME
if "%HOME%" == "" (set HOME=%HOMEDRIVE%%HOMEPATH%)

set ERROR_CODE=0

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

@REM Decide how to startup depending on the version of windows

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

@REM -- 4NT shell
if "%@eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set CMD_LINE_ARGS=%*
goto endInit

@REM The 4NT Shell from jp software
:4NTArgs
set CMD_LINE_ARGS=%$
goto endInit

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of arguments (up to the command line limit, anyway).
set CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto endInit
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto Win9xApp

@REM Reaching here means variables are defined and arguments have been captured
:endInit


@REM Start program
:runm2
SET CMDLINE=%~dp0\.%~n0.aux.exe %CMD_LINE_ARGS%
%CMDLINE%
if ERRORLEVEL 1 goto error
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
set ERROR_CODE=1

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set CMD_LINE_ARGS=
set CMDLINE=
goto postExec

:endNT
@endlocal

:postExec
exit /B %ERROR_CODE%
PK  2��R             	 META-INF/coursier/info.jsonUT �n�`U�͊�0��y�`z\�w?Goe��&�!))%���m	eo��a>�w}ok�b|k^sfe���s곢�=mO���[��*�k��چ� n�fP��R��Sfy��Ѩ3�ee!d�OeC$AQt�1��!�aA>ӕq\)��[T�x�f�e�P�;'����M�c��m�}��|� ���ƾN���PK;�e��   /  PK  2��R             	 META-INF/coursier/lock-fileUT �n�`E�;�  �ä��D;��9@�]��0z��y՛[�� �^�|����G��
�+WH���|�TR[�*��_މ�Ĺl�H���|�B�%���޹�:�z��cdk��JET�PK���nr   �   PK  2��R            " 	 META-INF/coursier/info-source.jsonUT �n�`E�Aj1E����f�u��l�)��I�m,��Pz�:�EV��O�����K�>\�S)��*�����.]�{��^�[U�څ�t�>�,����B^)�A�k�X���(����d�0���8���&��(/����*���8* u����i�)���C4�<���z7k ^Ο�A+?L`K� PKaRҷ   �   PK   2��R;�e��   /   	               META-INF/coursier/info.jsonUT �n�`PK   2��R���nr   �    	             META-INF/coursier/lock-fileUT �n�`PK   2��RaRҷ   �   " 	           �  META-INF/coursier/info-source.jsonUT �n�`PK      �   �    