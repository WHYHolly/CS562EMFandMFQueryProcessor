select prod, month, sum(x.quant)*3.5, sum(y.quant)
from sales
where year = 2004
group by prod, month; x, y
such that x.prod = prod and x.month = month,
y.prod = prod