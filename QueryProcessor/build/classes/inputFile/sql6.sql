select prod, quant
from sales
group by prod, quant; x, y
such that x.prod = prod,
y.prod = prod and y.quant < quant
having count(y.prod) * 2 = count(x.prod)