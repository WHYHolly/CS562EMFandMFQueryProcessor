select prod, quant, avg(x.quant) / avg(y.quant) * 2
from sales
group by prod, quant; x, y
such that x.prod = prod,
y.prod = prod