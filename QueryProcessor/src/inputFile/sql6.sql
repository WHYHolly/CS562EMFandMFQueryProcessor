select cust, prod, count(*)
from sales
where year = 2004
group by prod, cust