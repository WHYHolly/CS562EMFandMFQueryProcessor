select cust, avg(x.quant), avg(y.quant), avg(z.quant)
from sales
where year = 2004
group by cust; x, y, z
such that x.cust = cust and x.state = 'NY',
y.cust = cust and y.state = 'NJ',
z.cust = cust and z.state = 'CT'
having avg(x.quant) > avg(y.quant) and avg(x.quant) > avg(z.quant)