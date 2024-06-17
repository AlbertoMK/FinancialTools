### Pasos para poder usar el proyecto
1. Clona el proyecto con la url http  
2. Abre una Query SQL en tu IDE  
3. Pega el siguiente código SQL para crear tu base de datos vacía
   ``` sql
   create table AccionETF
   (
    id     INTEGER
        primary key,
    nombre VARCHAR,
    ticker VARCHAR
   );

   create table CompraAccion
   (
    id              INTEGER
        primary key autoincrement,
    participaciones DECIMAL,
    precio          DECIMAL,
    fecha           DATE,
    comision        DECIMAL,
    esCompra        BOOLEAN,
    accionETF_id    INTEGER
        references AccionETF
   );

   create table SiguienteId
   (
    siguienteId INTEGER
   );

   create table VentaDeposito
   (
    id           INTEGER
        primary key autoincrement,
    fecha        DATE,
    comision     DECIMAL,
    importeVenta DECIMAL
   );

   create table Deposito
   (
    id                INTEGER
        primary key,
    fechaContratacion DATE,
    desembolso        DECIMAL,
    comisionCompra    DECIMAL,
    tae               DECIMAL,
    nombre            VARCHAR,
    venta_id          INTEGER
        references VentaDeposito
   );

   create table RetribucionDeposito
   (
    id          INTEGER
        primary key autoincrement,
    importe     DECIMAL,
    fecha       DATE,
    deposito_id INTEGER
        references Deposito
   );
   ```
   Si tienes problemas con la consola SQL (inténtalo antes por tu cuenta melón), puedes pedir ayuda y se te pasará el fichero con la base de datos vacía para que puedas trabajar.  
   4. Crea tu rama a partir de la rama master y... ¡A programar!  
   5. Cuando creas que has terminado tu super cambio, puedes hacer una solicitud para unirla a la rama principal del proyecto y que el resto contemos con él en nuestro programa. Si todo ha ido bien, en 24h estará incorporada!
   6. Todo listo? Te veo en [qué cosas puedo hacer para mejorar el proyecto?](Funciones.md)
