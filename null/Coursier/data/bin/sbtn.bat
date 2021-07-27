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
PK  7��R             	 META-INF/coursier/info.jsonUT �n�`U��n�0Dw�+tkm���lAJfm2%���$�W�t�pw�H>��w�%+YBuS�^u#�	$W��w�f,�3r��eYF��`�`S��j����z���r��u�@:������6Xн�"vJ֨��լ���B��a�y���k���9�8e����<P�2���r��>Z)��kV곿Y��ug�PK��ƥ�     PK  7��R             	 META-INF/coursier/lock-fileUT �n�`�A� @��t-Àp�]�=�����B
���]��%n��`Z��b��������
��aCa��fwKKy�_���׌A"y�޲�I���ON;6dH�D��PK���g   p   PK  7��R            " 	 META-INF/coursier/info-source.jsonUT �n�`E�Aj1E����f�u��l�	��I�m,��Pz���EV�������'W�����J�
n�N��m�4�.6ԛ���$.D��+�\(�1�Ih�%��7r��2�mܱ��r�	�ݖB	&1������nx7���W}Wq���9�S�8�g̚�}�Ex6Ѵ�4��C�K �s��O\�z3�-\��PKd�N"�   �   PK   7��R��ƥ�      	               META-INF/coursier/info.jsonUT �n�`PK   7��R���g   p    	           	  META-INF/coursier/lock-fileUT �n�`PK   7��Rd�N"�   �   " 	           �  META-INF/coursier/info-source.jsonUT �n�`PK      �   �    