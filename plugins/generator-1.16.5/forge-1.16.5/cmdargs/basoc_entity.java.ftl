<#include "argprocedures.java.ftl">
.then(Commands.argument("${field$name}", EntityArgument.${field$type}())<#if statement$args??>${statement$args}</#if><@procedureCode field$procedure/>)