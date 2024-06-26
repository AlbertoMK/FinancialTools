# Requisitos pendientes  
- [ ] Buenas y malas decisiones (con parámetro para seleccionar cuantas decisiones mostrar) y otro opcional para no contar con los de precio 0. (fácil)
- [x] Rentabilidad anualizada
- [x] Añadir filtro periodo a rentabilidad
- [x] Comando para mostrar cartera segmentada y valores actuales de cada activo
- [x] Calculadora interés compuesto
- [ ] Calculadora valor presente
- [ ] Calculadora de libertad financiera
- [ ] Buscador de acciones con varios filtros como puede ser ratios, capitalización, rendimiento de dividendos... (difícil)
- [ ] Analizador de una empresa (ratios, tendencias...)
- [ ] Análisis de volatilidad (de una empresa o cartera)
- [ ] Módulo de recomendaciones y asesoría personalizado basado en el riesgo asumido (difícil)
- [ ] Comparador de acciones / etf
- [ ] Backtesting: Permite a los usuarios "jugar" con sus estrategias con datos históricos (media)
- [ ] Simulación de mercado: Permite a los usuarios ver los distintos resultados que tomaría su cartera asumiendo ciertos escenarios a futuro (difícil)
- [ ] Sistema de alertas por bajadas/subidas de precios (si se implantase en dispositivos móviles)
- [ ] Análisis de sentimiento de mercado (usando las recomendaciones de inversión de yahoo finance) (muy difícil)
- [ ] Modelo de correlación entre acciones, etfs o de la cartera entera (mide la diversidad o lo mucho o poco que están relacionadas dos empresas/etf)
- [ ] Maximizador de eficiencia siguiendo el modelo de Markowitz. Usa muchos cálculos y valoraciones para maximizar el cociente rentabilidad/riesgo. (muy difícil)
- [ ] Simulador de Monte Carlo con parámetros como las acciones que vas a considerar, el tiempo pasado sobre el que basarse y el tiempo futuro que predecir. (difícil)
- [ ] Construcción de robo-advisor siguiendo las tendencias de mercado, riesgo del usuario, diversificación... (se apoya mucho en los módulos mencionados anteriormente) (muy difícil)

# Mejoras de rendimiento
- [ ] Guardar una lista de festivos y antes de acceder a un valor en bolsa, comprobar que si el tipo no es criptomoneda (el mercado no cierra) y es festivo, sábado o domingo, modificar las fechas antes de acceder a los datos
- [ ] Al calcular la rentabilidad del segundo periodo, no volver a calcular desde el inicio y aprovecharse de lo que ya se ha calculado.
- [ ] Modificar la función de getSector del script de python para que reciba un conjunto de tickers y así hacerlo más rápido. Imprimir el resultado en formato JSON

# Errores por corregir
