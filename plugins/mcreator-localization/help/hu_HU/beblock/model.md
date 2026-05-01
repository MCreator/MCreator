Válaszd ki a blokknál használandó modellt. A modell csak a vizuális megjelenést határozza meg, nem a blokk bounding boxát.

- **Normál** – Normál blokk, mindkét oldalán textúrával
- Kereszt – X alakú, virágszerű mintázatú blokk.
  - Ha ezt a modellt használta, a textúra villódzásának elkerülése érdekében ajánlott az `alpha_test_single_sided`, a `blend` vagy az `opaque` renderelési módszert választani.
- Egységes textúra – Normál blokk, amelynek minden oldala ugyanazt a textúrát viseli
- Egyéni – egyéni Bedrock-modelleket (`.geo.json` fájlokat) is definiálhatsz. A blokk mérete legfeljebb 30×30×30 pixel lehet.