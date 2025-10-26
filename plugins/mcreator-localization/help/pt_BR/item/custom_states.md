Aqui você pode listar propriedades adicionais deste item e especificar como sua textura/modelo muda dependendo da combinação de valores de propriedades que formam um estado.

Uma propriedade de item pode receber qualquer número (inteiro ou fracionário) como seu valor, portanto, para evitar a necessidade de seguir qualquer granularidade e permitir o fornecimento de valores próximos, um estado corresponde se os valores reais da propriedade extraídos do item forem _iguais aos ou maiores que_ os valores esperados (especificados aqui).

Se houver vários estados com valores correspondentes, o último deles será usado. Se nenhum estado corresponder, o item usará sua aparência visual padrão.

Juntamente com as personalizadas, você também pode usar algumas propriedades de item embutidas definidas para todos os itens pertencentes do Minecraft.

NOTA: Estados duplicados não são permitidos. Se dois ou mais estados só se diferenciarem no valor de uma única propriedade, então remover essa propriedade automaticamente removerá as duplicatas do primeiro desses estados.