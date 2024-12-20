Este procedimento é executado quando o jogador clica com o botão direito em um bloco com o item na sua mão.

O procedimento deve retornar um resultado de ação do tipo SUCESSO/CONSUMIR se o item interagiu com o bloco, FALHA se a interação falhou, e PASSAR se não houve nenhuma interação.

Se o procedimento não retornar nenhum valor, o tipo do resultado de ação será PASSAR.

Se você deseja que o procedimento "${l10n.t("elementgui.common.event_right_clicked_air")}" seja apenas chamado quando a entidade clica com o botão direito no ar com este item, o procedimento deve sempre retornar SUCESSO/CONSUMIR.