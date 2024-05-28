import sys
import yfinance as yf

operacion = sys.argv[1]
ticker = sys.argv[2]

# imprime con formato [precio],[divisa]
def getPrecio(ticker):
    activo = yf.Ticker(ticker)
    info = activo.history(period='1d')
    precio = info['Close'].iloc[-1]
    return precio,activo.info['currency']

# imprime en cada línea y en orden con formato [precio],[divisa]
def getPrecios(tickers):
    datos = yf.download(tickers)
    precios = datos['Close'].iloc[-1]
    divisas = []
    for ticker in tickers:
        activo = yf.Ticker(ticker)
        divisas.append(activo.info['currency'])
    for precio, divisa in zip(precios, divisas):
        print(str(precio)+","+divisa)

if __name__ == "__main__":
    if operacion == "getPrecio":
        args = getPrecio(ticker)
        print(str(args[0])+","+args[1])
    elif operacion == "getPrecios":
        tickers = ticker.split(',')
        getPrecios(tickers)