Wählen Sie das Modell, das mit diesem Block verwendet werden soll. Modell definiert nur das visuelle Aussehen und nicht die
Begrenzungsbox des Blocks.

- **Normal** – normaler Block mit Texturen auf jeder Seite
- Kreuz - Block mit Texturen in X-Form wie Blumen.
  - Wenn du dieses Modell benutzt solltest du entweder `alpha_test_single_sided`, `blend` oder `opaque` als Rendering-Methode verwenden, um Texturflickern zu vermeiden.
- Einzelne Textur - Normaler Block mit der gleichen Textur auf jeder Seite
- Benutzerdefiniert - Du kannst auch benutzerdefinierte Bedrock-Modelle ('.geo.json' Dateien) definieren. Dein Block ist auf 30×30×30 Pixel in der Größe beschränkt.