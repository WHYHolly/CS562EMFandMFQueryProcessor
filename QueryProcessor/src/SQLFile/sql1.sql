select cust, sum(x.quant), sum(y.quant), sum(z.quant) 
from sales where yr=1997 
group by cust
such that x.state = 'NY',
          y.state = 'NJ',
          z.state = 'NJ'

having sum(x.quant) > 2 * sum(y.quant)


select cust, sum(x.quant), sum(y.quant), sum(z.quant)
from sales
where yr = 1997

select cust, sum(x.quant), sum(y.quant), sum(z.quant)
from sales
such that x.state = 'NY',
          y.state = 'NJ',
          z.state = 'NJ'

select cust, sum(x.quant), sum(y.quant), sum(z.quant)
from sales
such that x.state = 'NY',
          y.state = 'NJ',
          z.state = 'NJ'
having sum(x.quant) > 2 * sum(y.quant)