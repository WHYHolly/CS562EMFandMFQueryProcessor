-- for each prod count for each month of 2004 the sales that between the preview and following month's avgerage sale.

with temp1 as(
select y.prod, y.month, avg(x.quant) as avg1, avg(z.quant) as avg2
from sales as x, sales as y, sales as z
where x.prod = y.prod and z.prod = y.prod and x.month = y.month - 1 and z.month = y.month + 1 and x.year = 2004 and y.year = 2004 and z.year = 2004
group by y.prod, y.month)

select temp1.prod, temp1.month, count(*)
from sales, temp1
where sales.prod = temp1.prod and sales.month = temp1.month and sales.year = 2004 and sales.quant < avg2 and sales.quant > avg1
group by temp1.prod, temp1.month