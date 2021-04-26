<#if field$type == "string">
(StringArgumentType.getString(cmdargs, "${field$param}"))
<#elseif field$type == "message">
(new Object() {
            public String getMessage() {
                try {
                    return MessageArgument.getMessage(cmdargs, "${field$param}").getString();
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                    return "";
                }
            }
        }.getMessage())
</#if>