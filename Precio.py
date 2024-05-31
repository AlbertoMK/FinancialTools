import sys
import yfinance as yf

operacion = sys.argv[1]
ticker = sys.argv[2]

# EQUITY (accion), CRYPTOCURRENCY (crypto), ETF...
def getTipo(ticker):
    activo = yf.Ticker(ticker)
    print(activo.info["quoteType"])

def getSector(ticker):
    activo = yf.Ticker(ticker)
    print(activo.info["sector"])

if __name__ == "__main__":
    if operacion == "getTipo":
        getTipo(ticker)
    elif operacion == "getSector":
        getSector(ticker)