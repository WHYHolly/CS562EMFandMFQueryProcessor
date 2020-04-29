select cust, count(x.*)
from sales
group by cust;x
such that x.cust = cust