ManifestDPIAware true
Unicode true

!addplugindir "${NSIS_DIR}\Plugins\x86-unicode"
!addincludedir "${NSIS_DIR}\Include"

SetCompressor "lzma" ; to improve installer open performance and its size

!include "MUI2.nsh"

!define MCREATOR_VERSION "%mcreator%"
!define BUILD "%build%"
!define BITS "%bits%"

!searchreplace MCREATOR_VERSION_SHORT ${MCREATOR_VERSION} "." ""

Name "MCreator ${MCREATOR_VERSION}"
BrandingText "MCreator ${MCREATOR_VERSION}.${BUILD} - Developed by Pylo"

!define MUI_PRODUCT "MCreator"
!define MUI_ICON "..\..\platform\windows\installer\installer.ico"
!define MUI_UNICON "..\..\platform\windows\installer\uninstaller.ico"

RequestExecutionLevel admin

VIAddVersionKey ProductName      "MCreator ${MCREATOR_VERSION} Installer"
VIAddVersionKey Comments         "Installer for MCreator ${MCREATOR_VERSION}"
VIAddVersionKey CompanyName      "Pylo"
VIAddVersionKey FileVersion      "${MCREATOR_VERSION}.${BUILD}"
VIAddVersionKey LegalCopyright   "Copyright %year% (C) Pylo"
VIAddVersionKey FileDescription  "Installer for MCreator ${MCREATOR_VERSION}.${BUILD}"
VIProductVersion                 "${MCREATOR_VERSION}.${BUILD}.0"

OutFile "MCreator ${MCREATOR_VERSION} Windows ${BITS}bit.exe"

InstallDir "$PROGRAMFILES${BITS}\Pylo\MCreator"
!define INSTALLSIZE 306000

!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_BITMAP "..\..\platform\windows\installer\installer.bmp"
!define MUI_WELCOMEFINISHPAGE_BITMAP "..\..\platform\windows\installer\installer_side.bmp"
!define MUI_UNWELCOMEFINISHPAGE_BITMAP "..\..\platform\windows\installer\installer_side.bmp"

!define MUI_LICENSEPAGE_TEXT_TOP "Please read our terms of use published on our website. You can find the links below."

!define MUI_FINISHPAGE_RUN_TEXT "Start MCreator after finish"
!define MUI_FINISHPAGE_RUN "$INSTDIR\mcreator.exe"

!define MUI_FINISHPAGE_LINK "Donate and support MCreator project"
!define MUI_FINISHPAGE_LINK_LOCATION "http://mcreator.net/donate"

!define MUI_ABORTWARNING

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE ".\win${BITS}\LICENSE.txt"
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!define MUI_PAGE_CUSTOMFUNCTION_SHOW un.ModifyUnConfirm
!define MUI_PAGE_CUSTOMFUNCTION_LEAVE un.ModifyUnConfirmLeave
!insertmacro MUI_UNPAGE_WELCOME
UninstPage Custom un.LockedListShow un.LockedListLeave
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH

!insertmacro MUI_LANGUAGE "English"

Function .onInit
ReadRegStr $0 HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "UninstallString"

${If} $0 != ""
${AndIf} ${Cmd} `MessageBox MB_YESNO|MB_ICONQUESTION "Installer has detected a previous version of MCreator installed. \
                 If you intend to install the new version in the same folder as the \
                 old version, you NEED to uninstall the old version first. \
                 Do you want to uninstall previous version?" /SD IDYES IDYES`
	Call UninstallPrevious
${EndIf}
FunctionEnd

Section "MCreator ${MCREATOR_VERSION}" Installation
  SectionIn RO

  ;Add files
  SetOutPath "$INSTDIR"

  File /r "win${BITS}\*"

  ;create desktop shortcut
  CreateShortCut "$DESKTOP\MCreator.lnk" "$INSTDIR\mcreator.exe"

  ;create start menu entry
  CreateDirectory "$SMPROGRAMS\Pylo"
  CreateShortCut "$SMPROGRAMS\Pylo\MCreator.lnk" "$INSTDIR\mcreator.exe"

  ;write uninstall information to the registry
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "DisplayName" "MCreator ${MCREATOR_VERSION}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "Publisher" "Pylo"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "Version" "${MCREATOR_VERSION}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "URLInfoAbout" "https://mcreator.net/"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "HelpLink" "https://mcreator.net/wiki"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "URLUpdateInfo" "https://mcreator.net/download"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "UninstallString" "$INSTDIR\uninstall.exe"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "DisplayIcon" "$INSTDIR\mcreator.exe"
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "EstimatedSize" ${INSTALLSIZE}
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}}" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "NoRepair" 1

  ;add .mcreator file association
  WriteRegStr HKCR ".mcreator" "" "MCreatorWorkspaceFile"
  WriteRegStr HKCR "MCreatorWorkspaceFile" "" "MCreator workspace file"
  WriteRegStr HKCR "MCreatorWorkspaceFile\shell" "" "open"
  WriteRegStr HKCR "MCreatorWorkspaceFile\DefaultIcon" "" "$INSTDIR\mcreator.exe,0"
  WriteRegStr HKCR "MCreatorWorkspaceFile\shell\edit" "" "Edit this MCreator workspace"
  WriteRegStr HKCR "MCreatorWorkspaceFile\shell\edit\command" "" "$\"$INSTDIR\mcreator.exe$\" $\"%1$\""
  WriteRegStr HKCR "MCreatorWorkspaceFile\shell\open\command" "" "$\"$INSTDIR\mcreator.exe$\" $\"%1$\""

  WriteUninstaller "$INSTDIR\uninstall.exe"

SectionEnd

Var keepUserData
Var keepUserDataState

Function un.ModifyUnConfirm
    ${NSD_CreateCheckbox} 120u -25u 70% 20u "Keep settings and caches (useful when updating)"
    Pop $keepUserData
    SetCtlColors $keepUserData "" ${MUI_BGCOLOR}

    ${IfThen} $keepUserDataState == "" ${|} StrCpy $keepUserDataState 1 ${|}
    ${NSD_SetState} $keepUserData $keepUserDataState
FunctionEnd

Function un.ModifyUnConfirmLeave
    ${NSD_GetState} $keepUserData $keepUserDataState
FunctionEnd

Section "Uninstall"
  ;Delete Folders of MCreator
  RMDir /r "$INSTDIR\jdk\*.*"
  RMDir /r "$INSTDIR\lib\*.*"
  RMDir /r "$INSTDIR\license\*.*"
  RMDir /r "$INSTDIR\plugins\*.*"

  ;Delete Files of MCreator
  Delete "$INSTDIR\mcreator.exe"
  Delete "$INSTDIR\mcreator.bat"
  Delete "$INSTDIR\LICENSE.txt"

  ;Remove uninstaller
  Delete "$INSTDIR\uninstall.exe"

  ;Remove the installation directory
  RMDir "$INSTDIR"

  ;Remove shortcut
  Delete "$DESKTOP\MCreator.lnk"

  ;Remove start menu entry
  Delete "$SMPROGRAMS\Pylo\MCreator.lnk"
  RMDir "$SMPROGRAMS\Pylo"

  ;Delete user data if preserve option was not selected
  ${If} $keepUserDataState <> 1
    RMDir /r "$PROFILE\.mcreator\*.*"
  ${EndIf}

  ;Delete Uninstaller And Unistall Registry Entries
  DeleteRegKey HKLM "Software\${MUI_PRODUCT}"
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}"

  DeleteRegKey HKCR "MCreatorWorkspaceFile"
  DeleteRegKey HKCR ".mcreator"
SectionEnd

Function UninstallPrevious
    ; Check for uninstaller.
    ReadRegStr $0 HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "UninstallString"

    ${If} $0 != ""
        DetailPrint "Removing previous installation"

        Push $0
        Call GetParent
        Pop $1

        ; Run the uninstaller
        ExecWait '"$0" _?=$1'
    ${EndIf}
FunctionEnd

Function un.LockedListShow
  !insertmacro MUI_HEADER_TEXT 'Scanning for locked files' 'Clicking next will auto-close the programs listed below'
  LockedList::AddFile "$INSTDIR\jdk\jre\lib\rt.jar"
  LockedList::AddFolder "$INSTDIR\plugins"
  LockedList::AddFolder "$INSTDIR\lib"
  LockedList::Dialog /autonext /autoclosesilent
  Pop $R0
FunctionEnd

Function un.LockedListLeave
  StrCpy $R1 1
FunctionEnd

Function GetParent

  Exch $R0
  Push $R1
  Push $R2
  Push $R3

  StrCpy $R1 0
  StrLen $R2 $R0

  loop:
    IntOp $R1 $R1 + 1
    IntCmp $R1 $R2 get 0 get
    StrCpy $R3 $R0 1 -$R1
    StrCmp $R3 "\" get
  Goto loop

  get:
    StrCpy $R0 $R0 -$R1

    Pop $R3
    Pop $R2
    Pop $R1
    Exch $R0

FunctionEnd