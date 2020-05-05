select cust, month, avg(x.quant), avg(quant), avg(y.quant)
from sales
where year = 2004
group by cust, month; x, y
such that x.cust = cust and x.month < month,
y.cust = cust and y.month > month