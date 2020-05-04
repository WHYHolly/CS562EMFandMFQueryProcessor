select prod, month, avg(x.quant), avg(y.quant)
from sales
where year = 2004
group by prod, month; x, y
such that x.prod = prod and x.month = month - 1,
y.prod = prod and y.month = month + 1
