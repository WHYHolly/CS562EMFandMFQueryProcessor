select cust, prod, month, quant, day, year, state
from sales
group by cust, prod, month, quant, day, year, state