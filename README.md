### Pasos para poder usar el proyecto
1. Clona el proyecto:
   ``` git
   git clone https://github.com/AlbertoMK/FinancialTools.git
   ```
2. Abre la una Query SQL en tu IDE  
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
