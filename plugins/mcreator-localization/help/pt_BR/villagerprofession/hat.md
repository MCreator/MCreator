Este parâmetro especifica o tipo de chapéu definido pela textura da profissão selecionada, que controla se o aldeão ainda usará o chapéu definido por seu tipo após reivindicar esta profissão:
* **None:** Em todos os casos;
* **Partial:** Em casos em que não cobre a cabeça inteira do aldeão;
* **Full:** Em nenhum caso.

Essa condição pode ser representada pela tabela a seguir (onde TH é o chapéu dependente do tipo e PH é o chapéu da profissão):

| Visibilidade TH: | TH = None, | TH = Partial, | TH = Full |
| ---------------- |:----------:|:-------------:|:---------:|
| PH = None        |  Visível   |    Visível    |  Visível  |
| PH = Partial     |  Visível   |    Visível    | Escondido |
| PH = Full        | Escondido  |   Escondido   | Escondido |