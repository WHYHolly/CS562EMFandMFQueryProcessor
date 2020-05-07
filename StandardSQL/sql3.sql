-- for each customer and each month of 2004 the cust's avg sale before, during and after this month

with prev_mo as (
	select y.cust, y.month, avg(x.quant) as prev_q
	from sales as x, sales as y
	where x.year = 2004 and y.year = 2004 and x.cust = y.cust and x.month < y.month
	group by y.cust, y.month
),
cur_mo as (
	select cust, month, avg(quant) as cur_q
	from sales
	where year = 2004
	group by cust, month
),
post_mo as (
	select y.cust, y.month, avg(x.quant) as post_q
	from sales as x, sales as y
	where x.year = 2004 and y.year = 2004 and x.cust = y.cust and x.month > y.month
	group by y.cust, y.month
)

select *
from cur_mo left outer join prev_mo
			using(cust, month)
			left outer join post_mo
			using(cust, month)
order by cust