Questo parametro specifica il tipo di cappello definito dalla trama della professione selezionata, che controlla se l'abitante del villaggio indosserà ancora il cappello definito dal suo tipo dopo aver rivendicato questa professione:
* **Nessuno:** In tutti i casi;
* **Parziale:** Nei casi in cui non copre l'intera testa del villico;
* **Piena:** In nessun caso.

Questa condizione può essere rappresentata con la seguente tabella (dove TH è il cappello dipendente dal tipo e PH è il cappello della professione):

| Visibilità TH | TH = Niente | TH = Parziale | TH = Pieno |
| ------------- |:-----------:|:-------------:|:----------:|
| PH = Nessuno  |  Visibile   |   Visibile    |  Visibile  |
| PH = Parziale |  Visibile   |   Visibile    |  Nascosta  |
| PH = Pieno    |  Nascosta   |   Nascosta    |  Nascosta  |