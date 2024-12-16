O procedimento será executado quando o jogador utilizar o gatilho do portal em um bloco.

O procedimento deve retornar um resultado de ação do tipo SUCCESS/CONSUME se o gatilho interagiu com o bloco, FAIL se a interação falhou e PASS se não houve interação. Se o gatilho criou um portal com sucesso, ou se o procedimento não retornar nenhum valor, então o tipo de resultado da ação será SUCCESS.