<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2020 Pylo and contributors
 #
 # This program is free software: you can redistribute it and/or modify
 # it under the terms of the GNU General Public License as published by
 # the Free Software Foundation, either version 3 of the License, or
 # (at your option) any later version.
 #
 # This program is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 # GNU General Public License for more details.
 #
 # You should have received a copy of the GNU General Public License
 # along with this program.  If not, see <https://www.gnu.org/licenses/>.
 #
 # Additional permission for code generator templates (*.ftl files)
 #
 # As a special exception, you may create a larger work that contains part or
 # all of the MCreator code generator templates (*.ftl files) and distribute
 # that work under terms of your choice, so long as that work isn't itself a
 # template for code generation. Alternatively, if you modify or redistribute
 # the template itself, you may (at your option) remove this special exception,
 # which will cause the template and the resulting code generator output files
 # to be licensed under the GNU General Public License without this special
 # exception.
-->

<#-- @formatter:off -->

package ${package}.json;

public class ${name} {

  <#list data.values as value>
  private ${value.type} ${value.name};
  </#list>

  public ${name}(<#list data.values as value>${value.type} ${value.name}<#if value?has_next>,</#if></#list>){
    <#list data.values as value>
    this.${value.name} = ${value.name};
    </#list>
  }

  <#list data.values as value>
  public ${value.type} get${value.name}(){
    return ${value.name};
  }

  public void set${value.name}(${value.type} ${value.name}){
    this.${value.name} = ${value.name};
  }
  </#list>

}
<#-- @formatter:on -->
