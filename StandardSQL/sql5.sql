-- Compare for each customer and prod, the cust's avg sale of this prod and the average sale of the product to other customer.
with v1 as(
  select cust, prod, avg(quant) as avg1
  from sales
  group by cust, prod
),
v2 as(
  select x.prod, x.cust, avg(y.quant) as avg2
  from sales as x, sales as y
  where x.prod = y.prod and x.cust <> y.cust
  group by x.prod, x.cust
)

select *
from v1 full outer join v2
        using(cust, prod)
