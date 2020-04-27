select cust, sum(x.quant), sum(y.quant), sum(z.quant) 
from sales where yr=1997 
group by cust:x, y, z
such that x.state = 'NY' and x.cust = cust,
          y.state = 'NJ' and y.cust = cust and y.quant = avg(x.quant),
          z.state = 'CT' and z.cust = cust and z.quant = avg(x.quant) and z.quant = avg(y.quant)
having sum(x.quant) > 2 * sum(y.quant)
