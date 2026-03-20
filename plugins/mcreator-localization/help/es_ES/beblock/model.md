Selecciona el modelo que va a ser usado para este bloque. El modelo solo define el aspecto visual y no la caja de colisión del bloque.

- **Normal** - Bloque normal con texturas a cada lado
- Cruzado - Bloque con texturas en forma de X como las flores.
  - Si usaste este modelo, se recomienda usar `alpha_test_single_sided`, `blend` u `opaque` como método de renderizado para evitar la lucha de planos o parpadeo de la textura.
- Textura individual - Bloque normal con la misma textura en todos los lados
- Personalizado - también puedes definir modelos Bedrock personalizados (archivos `.geo.json`). Tu bloque está limitado a 30×30×30 píxeles de tamaño.