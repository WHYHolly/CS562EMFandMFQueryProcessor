select cust, prod, count(x.quant), avg(y.quant)
from sales
group by prod, cust; x, y
such that x.cust = cust and x.prod = prod,
y.cust<>cust and y.prod = prod