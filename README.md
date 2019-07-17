# Finpoup - Controle Financeiro Simples
Um [app Android](https://play.google.com/store/apps/details?id=br.com.rubensrodrigues.controlefinanceiro) de controle financeiro onde há 2 saldos (supérfluo e importante), onde você escolhe em qual deles você vai incluir as despesas e receitas.
## Ideia
A ideia do app surgiu pela necessidade de saber quanto que eu posso gastar com lazer sem prejudicar o meu orçamento. Então surgiu a ideia de criar um sistema onde eu possa alimentar 2 saldos que se chamam **supérfluo** e **importante**. Ao criar uma receita, é possível dividir o valor entre os saldos, por exemplo, 80% para **importante** e 20% para **supérfluo**, e então saber o quanto eu tenho para gastar com lazer sem lesar as coisas importante e com isso poupar.
## Tecnologias
- App construido em **Kotlin**. 
- Foi usado o _framework ORM_ **Room** para persistencia SQL.
- Usado **Retrofit** para consumir API Rest
## Visual
Consultei o **material design** para tornar o app o mais agradavel visualmente, então usei algumas bibliotecas que seguem o material, como:
- Floating Action Button (FAB) do [Dmytro Tarianyk](https://github.com/Clans/FloatingActionButton)
- Cardview do Google
- TextInputLayout com o estilo _outlined box_ azul do Google
- AppBarLayout para colapsar ao rolar tela/lista usando para esconder o banner de saldos. AppBar do Google
## Funcionalidade feitas
- CRUD das transações
- Listagem dos _cardviews_ das transações a esquerda (quando supérfluo) ou a direita (quando importante)
- Conversão de moeda ao usar os formulários de adição e edição de transações usando a [API](https://docs.awesomeapi.com.br/api-de-moedas) de moeda da AwesomeAPI
- Tela principal está usando 4 RecyclerViews usando _fragments_ para: todas as transações, apenas despesas, apenas receitas e transações futuras
- Banner mostrando 2 montantes dos totais de superfluo e importante, sento o total geral e o total por período escolhido
- Botão do _appbar_ para a escolha de períodos fixos, como por exemplo: últimos 7 dias, últimos 30 dias, este mês ou este ano 
## Funcionalidades futuras
- Listagem por período personalizada
- Relatórios
- Busca por título, categoria ou forma de pagamento
- Exportação das transações em CVS
- Importar o CVS
- Permitir que o usuário adicione categorias e formas de pagamento
- Notificação caso o transação que outrora futura se torne atual
## Autoria
Rubens Rodrigues (rubens_v2@hotmail.com)
## Deploy
Confira o app publicado no Google Play em https://play.google.com/store/apps/details?id=br.com.rubensrodrigues.controlefinanceiro
## Licença
                    GNU GENERAL PUBLIC LICENSE
                       Version 3, 29 June 2007

 Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 Everyone is permitted to copy and distribute verbatim copies
 of this license document, but changing it is not allowed.
