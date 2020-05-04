select prod, month, count(z.*)
from sales
where year = 2004
group by prod, month; x, y, z
such that x.prod = prod and x.month = month - 1,
y.prod = prod and y.month = month + 1,
z.prod = prod and z.month = month and z.quant > avg(x.quant) and z.quant < avg(y.quant)
