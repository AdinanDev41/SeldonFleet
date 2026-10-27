# PROTOCOLO.md — Decisões do Seldon Fleet

Este documento registra as decisões de classificação **antes** de o app exibir
resultados sobre o `frota.json` real.

## 1. Regra de classificação adotada

Adotei a regra de referência da especificação (seção 4), sem alterar limiares.
Motivo: com o prazo disponível, preferi uma regra simples, totalmente coberta
por testes de fronteira, a uma regra própria que eu não conseguiria validar.

**Derivadas** (após normalizar a energia para mAh):

- dreno absoluto (mAh/h) = energiaMah ÷ (screenOnMinutes ÷ 60)
- dreno relativo (%/h) = dreno absoluto × 100 ÷ batteryCapacityMah

O dreno relativo é usado, e não o absoluto, para comparar aparelhos de
capacidades diferentes.

**Ordem de avaliação** (o primeiro que casar define o status):

1. INDETERMINADO: capacidade ausente ou ≤ 0, OU screenOnMinutes ≤ 0,
   OU energia indisponível.
2. CRITICO: dreno relativo ≥ 12 %/h, OU crashCount7d ≥ 3, OU jankFramesPct ≥ 15.
3. ATENCAO: dreno relativo ≥ 7 %/h, OU crashCount7d ≥ 1, OU jankFramesPct ≥ 8.
4. OK: nenhuma das anteriores.

Os limiares são inclusivos (>=). Quando mais de um sinal casa no mesmo nível,
o primeiro na ordem dreno, falhas, quadros perdidos é o citado na justificativa.

## 2. Tratamento dos defeitos do arquivo

**Defeito 1: unidade de energia inconsistente (mAh e mWh).**
A conversão é feita na camada `data` (mapper), antes de qualquer cálculo:
mAh = mWh ÷ nominalVoltage. O domínio só enxerga mAh. Se a unidade for mWh
e a tensão for ausente ou ≤ 0, ou se a unidade for desconhecida, a energia
vira "indisponível" (nula) e o dispositivo é INDETERMINADO. Nenhum valor é inventado.

**Defeito 2: registros incompletos.**
Opção escolhida: classificar como INDETERMINADO (exibido como "Sem dados").
O dispositivo continua na lista, e a soma das contagens por status é igual ao
total de registros. Casos cobertos: capacidade 0 ou ausente, screenOnMinutes
0 ou ausente, energia indisponível. A divisão nunca é executada nesses casos,
portanto não há NaN, Infinity nem encerramento do app.

Alternativas descartadas: excluir o registro (esconde um aparelho da frota)
e falhar na ingestão (um registro ruim derrubaria a lista inteira).

## 3. Previsão registrada antes da execução

Calculei à mão a classificação dos 30 registros do arquivo recebido:

- INDETERMINADO (2): DEV-0311 (capacidade 0), DEV-0745 (tela 0)
- CRITICO (4): DEV-0522 (13,4 %/h), DEV-1839 (19,4), DEV-1924 (13,4), DEV-1985 (13,5)
- ATENCAO (8): DEV-0137 (2 falhas), DEV-1514 (7,4 %/h), DEV-1546 (7,5),
  DEV-1559 (10,1), DEV-1582 (10,2), DEV-1625 (7,6), DEV-1711 (9,7), DEV-1767 (10,8)
- OK (16): os demais

Total: 2 + 4 + 8 + 16 = 30. Se o app divergir, a divergência será investigada
e registrada, sem ajuste de limiar para "fazer bater".

Observação: sem a conversão de unidade, 8 dos 10 registros em mWh teriam o
status alterado (por exemplo, DEV-0204 sairia de OK para CRITICO).

## 4. Como prever o status de um registro

1. Converta a energia para mAh (se mWh: divida pela tensão nominal).
2. Se faltar capacidade, tela ou energia, ou se capacidade ou tela forem ≤ 0:
   INDETERMINADO.
3. Calcule o dreno relativo. Se ele for ≥ 12, ou houver ≥ 3 falhas, ou jank ≥ 15:
   CRITICO.
4. Senão, se dreno ≥ 7, ou ≥ 1 falha, ou jank ≥ 8: ATENCAO.
5. Senão: OK.

## 5. Ameaças à validade

- **O dreno assume que todo o consumo ocorreu com a tela ligada.** Aparelhos
  com serviço em primeiro plano e tela desligada têm o dreno superestimado e
  podem receber falso CRITICO ou ATENCAO.
- **INDETERMINADO esconde outros sinais.** O DEV-0311 tem jank de 11,2 % e uma
  falha, que o levariam a ATENCAO, mas a falta de capacidade encobre isso.
- **Limiares não calibrados com dados reais.** Nenhum registro do arquivo
  dispara falhas ≥ 3 nem jank ≥ 15; esses limiares só são exercitados pelos
  testes, não pelos dados. Todo CRITICO do arquivo vem do dreno.
- **Um único instantâneo.** O arquivo é uma exportação pontual; picos
  momentâneos e tendências não aparecem. O campo lastSeen não entra na regra
  (DEV-0745 está sem contato desde 18/10 e é classificado só pelos dados que tem).
- **A previsão da seção 3 foi calculada à mão**, então pode conter erro
  aritmético. Mitigação: testes unitários de fronteira e conferência no app.

## 6. Resultado observado

Executado em Xiaomi Redmi 12. O app exibiu OK: 16, Atenção: 8,
Crítico: 4, Sem dados: 2 (total 30), coincidindo com a previsão da seção 3.

Verificações manuais adicionais:

- Detalhe de um dispositivo Atenção (DEV-1711): justificativa "dreno de 9,7 %/h,
  igual ou acima do limiar de 7 %/h", coerente com o cálculo da previsão.
- Filtro preservado ao voltar do detalhe pelo botão do sistema.
- Favoritos mantidos após encerrar o processo do app e reabri-lo.