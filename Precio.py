import sys
import yfinance as yf

operacion = sys.argv[1]
ticker = sys.argv[2]

def getPrecio(ticker):
    activo = yf.Ticker(ticker)
    info = activo.history(period='1h')
    precio = info['Close'].iloc[-1]
    return precio

def getPrecios(tickers):
    datos = yf.download(tickers)
    intermedio = datos['Close'].iloc[-1]
    for i in intermedio:
        print(i)

if __name__ == "__main__":
    if operacion == "getPrecio":
        print(getPrecio(ticker))
    elif operacion == "getPrecios":
        tickers = ticker.split(',')
        getPrecios(tickers)